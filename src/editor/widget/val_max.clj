(ns editor.widget.val-max
  (:require [clojure.core.edn-str :refer [->edn-str]]
            [clojure.edn :as edn]
            [editor.widget :as widget]
            [gdx.scenes.scene2d.ui.text-field :as text-field]))

(defmethod widget/create :s/val-max
  [schema v {:keys [ctx/skin]}]
  (text-field/create
   {:text (->edn-str v)
    :skin skin
    :actor/listeners {:listener/text-tooltip [(str schema) skin]}}))

(defmethod widget/value :s/val-max
  [_  widget _schemas]
  (edn/read-string (text-field/text widget)))
