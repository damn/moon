(ns moon.schema.val-max
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [clojure.edn :as edn]
            [moon.edn]
            [moon.val-max :as val-max])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextField)))

(defn malli-form [_ _schemas]
  val-max/schema)

(defn create
  [schema v {:keys [ctx/skin]}]
  (doto (TextField. (moon.edn/->str v) ^Skin skin)
    (.addListener (text-tooltip/create (str schema) skin))))

(defn value
  [_  widget _schemas]
  (edn/read-string (TextField/.getText widget)))
