(ns clojure.vis-ui.select-box
  (:import (com.kotcrab.vis.ui.widget VisSelectBox)))

(defn create [{:keys [items selected]}]
  (doto (VisSelectBox.)
    (.setItems ^"[Lcom.badlogic.gdx.scenes.scene2d.Actor;" (into-array items))
    (.setSelected selected)))

(def selected VisSelectBox/.getSelected)
