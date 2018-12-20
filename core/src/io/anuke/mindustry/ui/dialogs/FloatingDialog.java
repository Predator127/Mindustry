package io.anuke.mindustry.ui.dialogs;

import io.anuke.arc.Core;
import io.anuke.arc.Input.Keys;
import io.anuke.arc.utils.Align;
import io.anuke.mindustry.core.GameState.State;
import io.anuke.mindustry.game.EventType.ResizeEvent;
import io.anuke.mindustry.graphics.Palette;
import io.anuke.mindustry.net.Net;
import io.anuke.arc.core.Core;
import io.anuke.arc.core.Events;
import io.anuke.arc.scene.ui.Dialog;
import io.anuke.arc.scene.ui.ScrollPane;

import static io.anuke.mindustry.Vars.state;

public class FloatingDialog extends Dialog{
    private boolean wasPaused;
    protected boolean shouldPause;

    public FloatingDialog(String title){
        super(title, "dialog");
        setFillParent(true);
        title().setAlignment(Align.center);
        getTitleTable().row();
        getTitleTable().addImage("white", Palette.accent)
                .growX().height(3f).pad(4f);

        hidden(() -> {
            if(shouldPause && !state.is(State.menu)){
                if(!wasPaused || Net.active()){
                    state.set(State.playing);
                }
            }
        });

        shown(() -> {
            if(shouldPause && !state.is(State.menu)){
                wasPaused = state.is(State.paused);
                state.set(State.paused);
            }
        });

        boolean[] done = {false};

        shown(() -> Core.app.post(() ->
                forEach(child -> {
                    if(done[0]) return;

                    if(child instanceof ScrollPane){
                        Core.scene.setScrollFocus(child);
                        done[0] = true;
                    }
                })));
    }

    protected void onResize(Runnable run){
        Events.on(ResizeEvent.class, event -> {
            if(isShown()){
                run.run();
            }
        });
    }

    @Override
    public void addCloseButton(){
        buttons().addImageTextButton("$text.back", "icon-arrow-left", 30f, this::hide).size(230f, 64f);

        keyDown(key -> {
            if(key == Keys.ESCAPE || key == Keys.BACK) {
                Core.app.post(this::hide);
            }
        });
    }
}
