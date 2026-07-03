(ns clojure.gdx.widget.new
  (:import (com.badlogic.gdx.scenes.scene2d.ui Widget)))

(defn f [draw-fn]
  (proxy [Widget] []
    (draw [batch parent-alpha]
      (draw-fn this batch parent-alpha))))
