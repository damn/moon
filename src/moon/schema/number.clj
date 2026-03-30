(ns moon.schema.number
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [clojure.edn :as edn]
            [moon.edn])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextField)))

(defn malli-form [[_ predicate] _schemas]
  (case predicate
    :int     int?
    :nat-int nat-int?
    :any     number?
    :pos     pos?
    :pos-int pos-int?))

(defn create
  [schema v {:keys [ctx/skin]}]
  (doto (TextField. (moon.edn/->str v) ^Skin skin)
    (.addListener (text-tooltip/create (str schema) skin))))

(defn value
  [_  widget _schemas]
  (edn/read-string (TextField/.getText widget)))
