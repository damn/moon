(ns moon.schema.string
  (:require [moon.schema :as schema])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextField
                                               TextTooltip)))

(defmethod schema/malli-form :s/string [_ _schemas]
  :string)

(defmethod schema/create :s/string [schema v {:keys [^Skin ctx/skin]}]
  (doto (TextField. (str v) skin)
    (.addListener (TextTooltip. (str schema) skin))))

(defmethod schema/value :s/string [_ widget _schemas]
  (TextField/.getText widget))
