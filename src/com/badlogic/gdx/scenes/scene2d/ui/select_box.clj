(ns com.badlogic.gdx.scenes.scene2d.ui.select-box
  (:require [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin])
  (:import (com.badlogic.gdx.scenes.scene2d.ui SelectBox)))

(defn create [skin]
  (SelectBox. (skin/type-hint skin)))

(defn set-items! [^SelectBox select-box items]
  (.setItems select-box ^"[Ljava.lang.Object;" (into-array items)))

(defn set-selected! [^SelectBox select-box selected]
  (.setSelected select-box selected))

(defn get-selected [^SelectBox select-box]
  (.getSelected select-box))
