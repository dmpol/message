package chat.network;

public interface TCPConnectionListener {

    void onConnectionReady(TCPConnection topConnection); //запустили соединение и всё работает
    void onReceiveStrting(TCPConnection topConnection, String value);//соединение приняло входящую строку;
    void onDisconnect(TCPConnection topConnection);//соединение порвалось
    void onException(TCPConnection topConnection, Exception e);//возникло исключение
}
