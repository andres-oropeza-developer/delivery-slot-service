#!/bin/bash
set -e

GREEN='\033[0;32m'
NC='\033[0m'
OS="$(uname -s)"

echo ""
echo "============================================================"
echo "  Delivery Slot Service - Walmart Chile"
echo "============================================================"
echo ""

# ── Java ──────────────────────────────────────────────────────
echo "[1/4] Verificando Java 21..."
check_java() {
    command -v java &>/dev/null || return 1
    VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    [ "$VER" -ge 21 ] 2>/dev/null
}
if ! check_java; then
    echo "     Instalando Java 21..."
    if [ "$OS" = "Darwin" ]; then
        command -v brew &>/dev/null || /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
        brew install --cask temurin@21
        export JAVA_HOME=$(/usr/libexec/java_home -v 21 2>/dev/null)
        export PATH="$JAVA_HOME/bin:$PATH"
    else
        sudo apt-get update -q && sudo apt-get install -y openjdk-21-jdk 2>/dev/null || true
    fi
fi
echo -e "     ${GREEN}Java OK${NC}"

# ── Node.js ───────────────────────────────────────────────────
echo ""
echo "[2/4] Verificando Node.js..."
if ! command -v node &>/dev/null; then
    echo "     Instalando Node.js..."
    if [ "$OS" = "Darwin" ]; then
        brew install node
    else
        curl -fsSL https://deb.nodesource.com/setup_lts.x | sudo -E bash -
        sudo apt-get install -y nodejs 2>/dev/null || true
    fi
fi
echo -e "     ${GREEN}Node.js $(node --version) OK${NC}"

# ── Maven Wrapper ─────────────────────────────────────────────
echo ""
echo "[3/4] Preparando Maven..."
cd "$(dirname "$0")"
chmod +x mvnw

# Create .mvn/wrapper and download jar if missing
mkdir -p .mvn/wrapper

if [ ! -f ".mvn/wrapper/maven-wrapper.properties" ]; then
    cat > .mvn/wrapper/maven-wrapper.properties << 'PROPS'
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip
wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
PROPS
fi

if [ ! -f ".mvn/wrapper/maven-wrapper.jar" ]; then
    echo "     Descargando Maven Wrapper..."
    curl -fsSL "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar" \
         -o .mvn/wrapper/maven-wrapper.jar
fi

# Frontend dependencies
if [ -f "frontend/package.json" ]; then
    echo "     Instalando dependencias del frontend..."
    cd frontend && npm install --silent 2>/dev/null && cd ..
fi

./mvnw dependency:resolve -q 2>/dev/null || true

# ── Start services ────────────────────────────────────────────
echo ""
echo "[4/4] Iniciando servicios..."
echo ""
echo "============================================================"
echo -e "  React UI:      ${GREEN}http://localhost:5173${NC}"
echo -e "  Thymeleaf UI:  ${GREEN}http://localhost:8080${NC}"
echo -e "  API Docs:      ${GREEN}http://localhost:8080/swagger-ui.html${NC}"
echo -e "  H2 Console:    ${GREEN}http://localhost:8080/h2-console${NC}"
echo "============================================================"
echo ""

if [ -f "frontend/package.json" ]; then
    cd frontend && npm run dev &
    FRONTEND_PID=$!
    cd ..
fi

(sleep 12 && \
    (open "http://localhost:5173" 2>/dev/null || xdg-open "http://localhost:5173" 2>/dev/null || true) && \
    (open "http://localhost:8080" 2>/dev/null || xdg-open "http://localhost:8080" 2>/dev/null || true)) &

cleanup() {
    [ -n "$FRONTEND_PID" ] && kill $FRONTEND_PID 2>/dev/null || true
    exit 0
}
trap cleanup INT TERM

./mvnw spring-boot:run
cleanup