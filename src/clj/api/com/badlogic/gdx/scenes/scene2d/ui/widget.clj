(ns clj.api.com.badlogic.gdx.scenes.scene2d.ui.widget
  (:import (com.badlogic.gdx.scenes.scene2d.ui Widget)))

(defn create [draw-fn]
  (proxy [Widget] []
    (draw [batch parent-alpha]
      (draw-fn this batch parent-alpha))))
