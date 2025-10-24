(ns clojure.vis-ui.scroll-pane
  (:require [cdq.ui :as ui])
  (:import (com.badlogic.gdx.scenes.scene2d.ui ScrollPane)))

(defn create [actor]
  (ScrollPane. actor ui/skin))
