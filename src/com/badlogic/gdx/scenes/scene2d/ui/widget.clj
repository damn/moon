(ns com.badlogic.gdx.scenes.scene2d.ui.widget
  (:refer-clojure :exclude [new])
  (:import
           (com.badlogic.gdx.scenes.scene2d.ui Widget)
           ))

(defn new [draw-fn]
  (proxy [Widget] []
    (draw [batch parent-alpha]
      (draw-fn this batch parent-alpha))))
