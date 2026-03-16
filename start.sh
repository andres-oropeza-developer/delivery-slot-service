#!/bin/bash
set -e

GREEN='\033[0;32m'
NC='\033[0m'

echo ""
echo "============================================================"
echo "  Delivery Slot Service - Walmart Chile"
echo "============================================================"
echo ""

echo "[1/3] Verificando Java 21..."

check_java() {
    if command -v java &>/dev/null; then
        VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
        if [ "$VER" -ge 21 ] 2>/dev/null; then
            echo "     Java $VER OK"
            return 0
        fi
    fi
    return 1
}

if ! check_java; then
    echo "     Java 21 no encontrado. Instalando..."
    OS="$(uname -s)"
    if [ "$OS" = "Darwin" ]; then
        command -v brew &>/dev/null || /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
        brew install --cask temurin@21
        export JAVA_HOME=$(/usr/libexec/java_home -v 21 2>/dev/null)
        export PATH="$JAVA_HOME/bin:$PATH"
    else
        sudo apt-get update -q && sudo apt-get install -y openjdk-21-jdk 2>/dev/null || \
        sudo dnf install -y java-21-openjdk 2>/dev/null || \
        echo "Instala Java 21 desde https://adoptium.net y vuelve a ejecutar."
    fi
fi

cd "$(dirname "$0")"
chmod +x mvnw

echo ""
echo "[2/3] Descargando dependencias..."
./mvnw dependency:resolve -q 2>/dev/null || true

echo ""
echo "[3/3] Iniciando la aplicacion..."
echo ""
echo "============================================================"
echo -e "  Frontend:   ${GREEN}http://localhost:8080${NC}"
echo -e "  API Docs:   ${GREEN}http://localhost:8080/swagger-ui.html${NC}"
echo -e "  H2 Console: ${GREEN}http://localhost:8080/h2-console${NC}"
echo "============================================================"
echo ""

(sleep 10 && (open "http://localhost:8080" 2>/dev/null || xdg-open "http://localhost:8080" 2>/dev/null || true)) &

./mvnw spring-boot:run
