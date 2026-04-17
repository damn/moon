(ns clojure.gdx.scene2d.ui.widget
  (:import (com.badlogic.gdx.scenes.scene2d.ui Widget)))

(defn create [{:keys [draw!]}]
  (proxy [Widget] []
    (draw [batch parent-alpha]
      (draw! this batch parent-alpha))))
