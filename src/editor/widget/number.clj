(ns editor.widget.number
  (:require [clojure.edn-str :refer [->edn-str]]
            [clojure.edn :as edn]
            [editor.widget :as widget]
            [clojure.scene2d.actor.add-listener :refer [add-listener!]]
            [clojure.scene2d.ui.text-field :as text-field]
            [clojure.scene2d.ui.text-tooltip :as text-tooltip]))

(defmethod widget/create :s/number
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (->edn-str v) skin)
    (add-listener! (text-tooltip/create (str schema) skin))))

(defmethod widget/value :s/number
  [_  widget _schemas]
  (edn/read-string (text-field/text widget)))
