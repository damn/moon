(ns clojure.add-close-button
  (:require [clojure.window :as window]
            [clojure.actor :as actor]
            [clojure.utils-change-listener :as change-listener]
            [clojure.add-cell :refer [add-cell!]]
            [clojure.ui-text-button :as text-button]))

(defn f! [window skin]
  (add-cell! (window/get-title-table window)
             {:actor (doto (text-button/create {:text "X" :skin skin})
                       (actor/add-listener! (change-listener/create
                                       (fn [_event _actor]
                                         (actor/remove! window)))))}))
