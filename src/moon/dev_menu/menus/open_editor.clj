(ns moon.dev-menu.menus.open-editor
  (:require [clojure.string :as str]
            [moon.db :as db]
            [moon.ui.property-editor-window :as property-editor-window]
            [moon.ui.property-overview-window :as property-overview-window] )
  (:import (com.badlogic.gdx.scenes.scene2d Stage)))

(defn create [{:keys [ctx/db]}]
  {:label "Editor"
   :items (for [property-type (sort (db/property-types db))]
            {:label (str/capitalize (name property-type))
             :on-click (fn [_actor {:keys [ctx/db
                                           ctx/skin
                                           ctx/stage
                                           ctx/textures]}]
                         (Stage/.addActor stage
                                          (property-overview-window/create
                                           {:db db
                                            :textures textures
                                            :skin skin
                                            :property-type property-type
                                            :clicked-id-fn (fn [_actor id {:keys [ctx/stage] :as ctx}]
                                                             (Stage/.addActor stage
                                                                              (property-editor-window/create
                                                                               {:ctx ctx
                                                                                :property (db/get-raw db id)})))})))})})
