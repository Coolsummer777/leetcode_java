package sp_2026.airbnb;


public class P1041 {
    public boolean isRobotBounded(String instructions) {
        int[] dir = new int[2];
        int[] position = new int[]{0,0};
        dir[0] = 0;
        dir[1] = 1;

        for (int i=0;i<instructions.length();i++){
            char c = instructions.charAt(i);
            if (c == 'G'){
                position[0] += dir[0];
                position[1] += dir[1];
            }else if(c == 'L' || c == 'l'){
                turnLeft(dir);
            }else {
                turnRight(dir);
            }
        }

        if (position[0] == 0 && position [1] == 0){
            return true;
        }
        if (dir[0] == 0 && dir[1] == 1){
            return false;
        }

        return true;
    }

    private void turnLeft(int[] direction){
        if (direction[0] == 1 && direction[1] == 0){direction[0] = 0;direction[1] = 1;return;} 
        if (direction[0] == 0 && direction[1] == 1){direction[0] = -1;direction[1] = 0;return;} 
        if (direction[0] == -1 && direction[1] == 0){direction[0] = 0;direction[1] = -1;return;} 
        if (direction[0] == 0 && direction[1] == -1){direction[0] = 1;direction[1] = 0;return;} 
    }

        private void turnRight(int[] direction){
        if (direction[0] == 1 && direction[1] == 0){direction[0] = 0;direction[1] = -1;return;} 
        if (direction[0] == 0 && direction[1] == -1){direction[0] = -1;direction[1] = 0;return;} 
        if (direction[0] == -1 && direction[1] == 0){direction[0] = 0;direction[1] = 1;return;} 
        if (direction[0] == 0 && direction[1] == 1){direction[0] = 1;direction[1] = 0;return;} 
    }
}
