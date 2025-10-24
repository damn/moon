(ns clojure.gdx.scene2d.ui.widget
  (:import (com.badlogic.gdx.scenes.scene2d.ui Widget)))

(defn create [draw]
  (proxy [Widget] []
    (draw [batch parent-alpha]
      (draw this batch parent-alpha))))

(defn set-fill-parent! [widget fill-parent?]
  (Widget/.setFillParent widget fill-parent?))
