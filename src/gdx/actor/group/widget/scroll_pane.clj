(ns gdx.actor.group.widget.scroll-pane
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui ScrollPane Skin)))

(defn create [widget skin]
  (ScrollPane. ^Actor widget ^Skin skin))
