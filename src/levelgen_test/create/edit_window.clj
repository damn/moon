(ns levelgen-test.create.edit-window
  (:require [scene2d.ui.text-button :as text-button]
            [scene2d.utils.change-listener :as change-listener]
            [levelgen-test.generate-level :as generate-level])
  (:import (com.badlogic.gdx.scenes.scene2d Actor
                                            Event)))

(defn edit-window [skin level-fns]
  {:title "Edit"
   :skin skin
   :table/rows (for [level-fn level-fns
                     :let [on-clicked (fn [actor ctx]
                                        (let [stage (Actor/.getStage actor)
                                              new-ctx (generate-level/f ctx level-fn)]
                                          (set! (.ctx stage) new-ctx)))]]
                 [{:actor (doto (text-button/create {:text (str "Generate " level-fn) :skin skin})
                            (Actor/.addListener (change-listener/create
                                                 (fn [event actor]
                                                   (on-clicked actor (:stage/ctx (Event/.getStage event)))))))}])})
