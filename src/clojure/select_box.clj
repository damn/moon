(ns clojure.select-box
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.scenes.scene2d.ui SelectBox Skin)))

(defn get-selected [^SelectBox select-box]
  (SelectBox/.getSelected select-box))

(defn new [^Skin skin]
  (SelectBox. skin))

(defn set-items! [^SelectBox select-box items]
  (SelectBox/.setItems select-box ^"[Ljava.lang.Object;" (into-array items)))

(defn set-selected! [^SelectBox select-box selected]
  (SelectBox/.setSelected select-box selected))
