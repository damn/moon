(ns levelgen-test.create.edit-window
  (:require [scene2d.event.get-stage :as get-stage]
            [scene2d.actor.get-stage :as actor-stage]
            [scene2d.actor.add-listener :refer [add-listener!]]
            [scene2d.ui.text-button :as text-button]
            [scene2d.utils.change-listener :as change-listener]
            [scene2d.stage.set-ctx :refer [set-ctx!]]
            [levelgen-test.generate-level :as generate-level]))

(defn edit-window [skin level-fns]
  {:title "Edit"
   :skin skin
   :table/rows (for [level-fn level-fns
                     :let [on-clicked (fn [actor ctx]
                                        (let [stage (actor-stage/f actor)
                                              new-ctx (generate-level/f ctx level-fn)]
                                          (set-ctx! stage new-ctx)))]]
                 [{:actor (doto (text-button/create {:text (str "Generate " level-fn) :skin skin})
                            (add-listener! (change-listener/create
                                            (fn [event actor]
                                              (on-clicked actor (:stage/ctx (get-stage/f event)))))))}])})
