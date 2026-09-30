(ns gdx.actor.widget.select-box
  (:import (com.badlogic.gdx.scenes.scene2d.ui SelectBox Skin)))

(defn create [skin]
  (SelectBox. ^Skin skin))

(defn get-selected [select-box]
  (.getSelected ^SelectBox select-box))

(defn set-items! [select-box items]
  (.setItems ^SelectBox select-box ^"[Ljava.lang.Object;" (into-array items)))

(defn set-selected! [select-box item]
  (.setSelected ^SelectBox select-box item))
