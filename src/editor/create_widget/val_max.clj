(ns editor.create-widget.val-max
  (:require [clojure.edn-str :refer [->edn-str]]
            [clojure.gdx :as gdx]
            [scene2d.ui.text-field :as text-field]
            [scene2d.ui.text-tooltip :as text-tooltip]))

(defn f
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (->edn-str v) skin)
    (gdx/add-listener! (text-tooltip/create (str schema) skin))))
