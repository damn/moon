(ns clojure.editor.widget.string
  (:require [gdl.add-listener :refer [add-listener!]]
            [gdl.text-field :as text-field]
            [gdl.text-tooltip :as text-tooltip]))

(defn create
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (str v) skin)
    (add-listener! (text-tooltip/create (str schema) skin))))

(defn value
  [_ widget _schemas]
  (text-field/text widget))
