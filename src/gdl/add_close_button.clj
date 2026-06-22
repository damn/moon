(ns gdl.add-close-button
  (:require [gdl.add-listener :refer [add-listener!]]
            [gdl.remove :refer [remove!]]
            [gdl.change-listener :as change-listener]
            [gdl.add-cell :refer [add-cell!]]
            [gdl.text-button :as text-button]
            [gdl.get-title-table :as get-title-table]))

(defn f! [window skin]
  (add-cell! (get-title-table/f window)
             {:actor (doto (text-button/create {:text "X" :skin skin})
                       (add-listener! (change-listener/create
                                       (fn [_event _actor]
                                         (remove! window)))))}))
