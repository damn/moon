(ns com.badlogic.gdx.scenes.scene2d.ui.select-box
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.scenes.scene2d.ui SelectBox Skin)))

(defn new [^Skin skin]
  (SelectBox. skin))

(defn getSelected [^SelectBox select-box]
  (.getSelected select-box))

(defn setItems [^SelectBox select-box items]
  (.setItems select-box ^"[Ljava.lang.Object;" (into-array items)))

(defn setSelected [^SelectBox select-box selected]
  (.setSelected select-box selected))
