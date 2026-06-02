(ns levelgen-test.create.edit-window
  (:require [clojure.gdx.scene2d.event :as event]
            [clojure.gdx.scene2d.ui.text-button :as text-button]
            [clojure.gdx.scene2d.utils.change-listener :as change-listener]
            [gdx.stage :as stage]
            [levelgen-test.generate-level :as generate-level]))

(defn edit-window [skin level-fns]
  {:title "Edit"
   :skin skin
   :table/rows (for [level-fn level-fns
                     :let [on-clicked (fn [actor ctx]
                                        (let [stage (.getStage actor)
                                              new-ctx (generate-level/f ctx level-fn)]
                                          (stage/set-ctx! stage new-ctx)))]]
                 [{:actor (text-button/create
                           {:text (str "Generate " level-fn)
                            :skin skin
                            :actor/listeners [(change-listener/create
                                               (fn [event actor]
                                                 (on-clicked actor (:stage/ctx (event/stage event)))))]})}])})
