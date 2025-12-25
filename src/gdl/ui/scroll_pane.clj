(ns gdl.ui.scroll-pane
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui ScrollPane
                                               Skin)))

(defn create [^Actor actor ^Skin skin]
  (ScrollPane. actor skin))
