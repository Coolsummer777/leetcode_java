package sp_2026.airbnb.coding;

public class TerrainRender {

    public static void handle(){
        int[] terrain = new int[] {5, 4, 3, 2, 1, 3, 4, 0, 3, 4};
        render(terrain);
    }

    public static void render(int[] terrain) {
        int maxHeight = 0;
        for (int height : terrain) {
            maxHeight = Math.max(maxHeight, height);
        }

        for (int i = maxHeight - 1; i >= 0; i--) {
            for (int j=0;j<terrain.length;j++){
                if (terrain[j] > i) {
                    System.out.print("+");
                } else {
                    System.out.print(" ");
                }
            }


            System.out.println();
        }
    }
}
