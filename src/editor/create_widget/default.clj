(ns editor.create-widget.default
  (:require [string.truncate :refer [truncate]]
            [clojure.edn-str :refer [->edn-str]]
            [scene2d.ui.label :as label]))

(defn f
  [_ v {:keys [ctx/skin]}]
  (label/create
   {:text (truncate (->edn-str v) 60)
    :skin skin}))
