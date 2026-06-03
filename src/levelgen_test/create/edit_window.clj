(ns levelgen-test.create.edit-window
  (:require [clojure.gdx.scene2d.actor.get-stage :refer [get-stage]]
            [clojure.gdx.scene2d.actor.add-listener :refer [add-listener!]]
            [clojure.gdx.scene2d.event :as event]
            [clojure.gdx.scene2d.ui.text-button :as text-button]
            [clojure.gdx.scene2d.utils.change-listener :as change-listener]
            [clojure.gdx.scene2d.stage.set-ctx :refer [set-ctx!]]
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
                                              (on-clicked actor (:stage/ctx (event/stage event)))))))}])})
