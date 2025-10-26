(ns moon.ui.scroll-pane
  (:require [moon.ui :as ui])
  (:import (com.badlogic.gdx.scenes.scene2d.ui ScrollPane)))

(defn create [actor]
  (ScrollPane. actor ui/skin))
