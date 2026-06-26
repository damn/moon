(ns com.badlogic.gdx.scenes.scene2d.ui.text-button
  (:require [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin])
  (:import (com.badlogic.gdx.scenes.scene2d.ui TextButton)))

(defn create [text skin]
  (TextButton. ^String text (skin/type-hint skin)))
