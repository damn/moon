(ns com.badlogic.gdx.scenes.scene2d.ui.label
  (:require [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label)))

(def java-class Label)

(defn create [text skin]
  (Label. ^String text (skin/type-hint skin)))

(defn set-text! [^Label label text]
  (.setText label ^String text))

(defn type-hint
  ^Label
  [obj]
  obj)
