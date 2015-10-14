package com.plo.ploworks.berita;

/**
 * Created by Farikh Fadlul Huda on 10/6/2015.
 */
public class Berita {
    String idPost;
    String kodeUser;
    String userName,nama;
    String judul,isiSingkat;
    String urlFotoUser,urlGambar;
    String jumlahKomentar;
    String komentarTerakhir;
    String waktu;

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getKodeUser() {
        return kodeUser;
    }

    public void setKodeUser(String kodeUser) {
        this.kodeUser = kodeUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getIsiSingkat() {
        return isiSingkat;
    }

    public void setIsiSingkat(String isiSingkat) {
        this.isiSingkat = isiSingkat;
    }

    public String getUrlFotoUser() {
        return urlFotoUser;
    }

    public void setUrlFotoUser(String urlFotoUser) {
        this.urlFotoUser = urlFotoUser;
    }

    public String getUrlGambar() {
        return urlGambar;
    }

    public void setUrlGambar(String urlGambar) {
        this.urlGambar = urlGambar;
    }

    public String getJumlahKomentar() {
        return jumlahKomentar;
    }

    public void setJumlahKomentar(String jumlahKomentar) {
        this.jumlahKomentar = jumlahKomentar;
    }

    public String getKomentarTerakhir() {
        return komentarTerakhir;
    }

    public void setKomentarTerakhir(String komentarTerakhir) {
        this.komentarTerakhir = komentarTerakhir;
    }
}
