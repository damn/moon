(ns moon.ui.select-box
  (:require [cdq.ui :as ui])
  (:import (com.badlogic.gdx.scenes.scene2d.ui SelectBox)))

(defn create [{:keys [items selected]}]
  (doto (SelectBox. ui/skin)
    (.setItems ^"[Lcom.badlogic.gdx.scenes.scene2d.Actor;" (into-array items))
    (.setSelected selected)))

(def selected SelectBox/.getSelected)
