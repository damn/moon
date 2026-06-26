(ns com.badlogic.gdx.scenes.scene2d.ui.check-box
  (:require [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin])
  (:import (com.badlogic.gdx.scenes.scene2d.ui CheckBox)))

(defn create [skin]
  (CheckBox. "" (skin/type-hint skin)))

(defn set-checked! [^CheckBox check-box checked?]
  (.setChecked check-box checked?))

(defn checked? [^CheckBox check-box]
  (.isChecked check-box))
