;; Copyright 2014-2016 Red Hat, Inc, and individual contributors.
;;
;; Licensed under the Apache License, Version 2.0 (the "License");
;; you may not use this file except in compliance with the License.
;; You may obtain a copy of the License at
;;
;; http://www.apache.org/licenses/LICENSE-2.0
;;
;; Unless required by applicable law or agreed to in writing, software
;; distributed under the License is distributed on an "AS IS" BASIS,
;; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;; See the License for the specific language governing permissions and
;; limitations under the License.

(ns atticboss.config-test
  (:require [clojure.test :refer :all])
  (:import [org.projectodd.atticboss.caching Caching$CreateOption Config]
           org.projectodd.atticboss.Options))

(deftest persistence
  (is (not (.. (Config/uration (Options.))
             persistence usingStores)))
  (is (not (.. (Config/uration (Options. {Caching$CreateOption/PERSIST false}))
             persistence usingStores)))
  (is (.. (Config/uration (Options. {Caching$CreateOption/PERSIST true}))
        persistence usingStores))
  (is (.. (Config/uration (Options. {Caching$CreateOption/PERSIST "foo"}))
        persistence usingStores)))
