(ns moon.schema.number
  (:require [clojure.edn :as edn]
            [moon.schema :as schema]
            [moon.utils :as utils])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextField
                                               TextTooltip)))

(defmethod schema/malli-form :s/number [[_ predicate] _schemas]
  (case predicate
    :int     int?
    :nat-int nat-int?
    :any     number?
    :pos     pos?
    :pos-int pos-int?))

(defmethod schema/create :s/number
  [schema v {:keys [^Skin ctx/skin]}]
  (doto (TextField. (utils/->edn-str v) skin)
    (.addListener (TextTooltip. (str schema) skin))))

(defmethod schema/value :s/number
  [_  widget _schemas]
  (edn/read-string (TextField/.getText widget)))
