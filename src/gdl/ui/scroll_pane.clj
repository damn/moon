(ns gdl.ui.scroll-pane
  (:import (com.badlogic.gdx.scenes.scene2d.ui ScrollPane)))

(defn create [actor skin]
  (ScrollPane. actor skin))
