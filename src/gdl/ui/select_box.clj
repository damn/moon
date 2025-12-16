(ns gdl.ui.select-box
  (:import (com.badlogic.gdx.scenes.scene2d.ui SelectBox)))

(defn create [{:keys [items selected]} skin]
  (doto (SelectBox. skin)
    (.setItems ^"[Lcom.badlogic.gdx.scenes.scene2d.Actor;" (into-array items))
    (.setSelected selected)))

(def selected SelectBox/.getSelected)
