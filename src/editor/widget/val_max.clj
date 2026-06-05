(ns editor.widget.val-max
  (:require [clojure.edn-str :refer [->edn-str]]
            [clojure.edn :as edn]
            [editor.widget :as widget]
            [clojure.gdx.scene2d.actor.add-listener :refer [add-listener!]]
            [clojure.gdx.scene2d.ui.text-field :as text-field]
            [clojure.gdx.scene2d.ui.text-tooltip :as text-tooltip]))

(defmethod widget/create :s/val-max
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (->edn-str v) skin)
    (add-listener! (text-tooltip/create (str schema) skin))))

(defmethod widget/value :s/val-max
  [_  widget _schemas]
  (edn/read-string (text-field/text widget)))
