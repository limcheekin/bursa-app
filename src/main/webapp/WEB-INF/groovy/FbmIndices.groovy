enum FbmIndices {
    FBMKLCI('FBMKLCI'),
    FBM70('FBM70'),
    FBMT100('FBMT100'),
    FBMSCAP('FBMSCAP'),
    FBMEMAS('FBMEMAS')

    FbmIndices(String value) {
        this.value = value
    }
    
    final String value
}