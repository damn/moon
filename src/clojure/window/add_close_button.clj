(ns clojure.window.add-close-button
  (:require
            [clojure.actor.add-listener]
            [clojure.actor.remove-actor] [clojure.window :as window]
            [clojure.utils-change-listener :as change-listener]
            [clojure.table.add-cell :refer [add-cell!]]
            [clojure.ui-text-button :as text-button]))

(defn f! [window skin]
  (add-cell! (window/get-title-table window)
             {:actor (doto (text-button/create {:text "X" :skin skin})
                       (clojure.actor.add-listener/f (change-listener/create
                                       (fn [_event _actor]
                                         (clojure.actor.remove-actor/f window)))))}))
