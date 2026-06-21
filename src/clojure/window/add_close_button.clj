(ns clojure.window.add-close-button
  (:require [clojure.actor.add-listener :refer [add-listener!]]
            [clojure.actor.remove :refer [remove!]]
            [clojure.change-listener :as change-listener]
            [clojure.ui.table.add-cell :refer [add-cell!]]
            [clojure.ui.text-button :as text-button]
            [clojure.window.get-title-table :as get-title-table]))

(defn f! [window skin]
  (add-cell! (get-title-table/f window)
             {:actor (doto (text-button/create {:text "X" :skin skin})
                       (add-listener! (change-listener/create
                                       (fn [_event _actor]
                                         (remove! window)))))}))
