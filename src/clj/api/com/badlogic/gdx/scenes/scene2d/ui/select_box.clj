(ns clj.api.com.badlogic.gdx.scenes.scene2d.ui.select-box
  (:import (com.badlogic.gdx.scenes.scene2d.ui SelectBox
                                               Skin)))

(defn create [^Skin skin]
  (SelectBox. ^Skin skin))

(defn set-items! [^SelectBox select-box items]
  (.setItems select-box ^"[Ljava.lang.Object;" (into-array items)))

(defn set-selected! [^SelectBox select-box item]
  (.setSelected select-box item))

(defn selected [^SelectBox select-box]
  (.getSelected select-box))
