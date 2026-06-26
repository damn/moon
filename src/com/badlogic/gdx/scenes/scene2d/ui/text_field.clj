(ns com.badlogic.gdx.scenes.scene2d.ui.text-field
  (:require [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin])
  (:import (com.badlogic.gdx.scenes.scene2d.ui TextField)))

(defn create [text skin]
  (TextField. ^String text (skin/type-hint skin)))

(defn get-text [^TextField text-field]
  (.getText text-field))
