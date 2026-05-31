(ns editor.widget.number
  (:require [clojure.core-ext :refer [->edn-str]]
            [clojure.edn :as edn]
            [editor.widget :as widget]
            [gdx.scenes.scene2d.ui.text-field :as text-field]))

(defmethod widget/create :s/number
  [schema v {:keys [ctx/skin]}]
  (text-field/create
   {:text (->edn-str v)
    :skin skin
    :actor/listeners {:listener/text-tooltip [(str schema) skin]}}))

(defmethod widget/value :s/number
  [_  widget _schemas]
  (edn/read-string (text-field/text widget)))
