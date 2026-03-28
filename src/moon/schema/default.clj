(ns moon.schema.default
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.label :as label]
            [moon.edn :as edn]
            [moon.string :as string])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn create
  [_ v {:keys [ctx/skin]}]
  (label/create (string/truncate (edn/->str v) 60) skin))

(defn value
  [_  widget _schemas]
  ((Actor/.getUserObject widget) 1))
