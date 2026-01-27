(ns moon.schema.default
  (:require [moon.edn :as edn]
            [moon.string :as string])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin)))

(defn create
  [_ v {:keys [ctx/skin]}]
  (Label. (string/truncate (edn/->str v) 60)
          ^Skin skin))

(defn value
  [_  widget _schemas]
  ((Actor/.getUserObject widget) 1))
