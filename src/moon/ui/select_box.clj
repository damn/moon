(ns moon.ui.select-box
  (:import (com.badlogic.gdx.scenes.scene2d.ui SelectBox
                                               Skin)))

(defn create [{:keys [items selected]} ^Skin skin]
  (doto (SelectBox. skin)
    (.setItems ^"[Lcom.badlogic.gdx.scenes.scene2d.Actor;" (into-array items))
    (.setSelected selected)))

(def selected SelectBox/.getSelected)
