(ns levelgen-test.create.edit-window
  (:require [clojure.actor.get-stage :refer [get-stage]]
            [clojure.actor.add-listener :refer [add-listener!]]
            [clojure.event.get-stage :as event]
            [clojure.ui.text-button :as text-button]
            [clojure.utils.change-listener :as change-listener]
            [clojure.stage.set-ctx :refer [set-ctx!]]
            [levelgen-test.generate-level :as generate-level]))

(defn edit-window [skin level-fns]
  {:title "Edit"
   :skin skin
   :table/rows (for [level-fn level-fns
                     :let [on-clicked (fn [actor ctx]
                                        (let [stage (get-stage actor)
                                              new-ctx (generate-level/f ctx level-fn)]
                                          (set-ctx! stage new-ctx)))]]
                 [{:actor (doto (text-button/create {:text (str "Generate " level-fn) :skin skin})
                            (add-listener! (change-listener/create
                                            (fn [event actor]
                                              (on-clicked actor (:stage/ctx (event/get-stage event)))))))}])})
