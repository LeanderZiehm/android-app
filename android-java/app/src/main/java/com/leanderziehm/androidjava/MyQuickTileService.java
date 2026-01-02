package com.leanderziehm.androidjava;

import android.app.PendingIntent;
import android.content.Intent;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

public class MyQuickTileService extends TileService {

    @Override
    public void onTileAdded() {
        // Called when the user adds the tile
        Toast.makeText(this, "Tile Added", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStartListening() {
        // Called when the tile becomes visible
        Tile tile = getQsTile();
        tile.setLabel("LeanderZiehm"); // Text under the tile
        tile.setState(Tile.STATE_INACTIVE); // Default state
        tile.updateTile();
    }

    @Override
    public void onClick() {
        super.onClick();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Launch activity and collapse quick settings
        startActivityAndCollapse(pendingIntent);
    }


}
