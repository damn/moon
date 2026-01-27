(ns moon.schema.number
  (:require [clojure.edn :as edn]
            [moon.edn])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextField
                                               TextTooltip)))

(defn malli-form [[_ predicate] _schemas]
  (case predicate
    :int     int?
    :nat-int nat-int?
    :any     number?
    :pos     pos?
    :pos-int pos-int?))

(defn create
  [schema v {:keys [^Skin ctx/skin]}]
  (doto (TextField. (moon.edn/->str v) skin)
    (.addListener (TextTooltip. (str schema) skin))))

(defn value
  [_  widget _schemas]
  (edn/read-string (TextField/.getText widget)))
