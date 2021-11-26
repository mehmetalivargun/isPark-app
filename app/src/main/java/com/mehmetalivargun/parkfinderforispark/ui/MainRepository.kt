package com.mehmetalivargun.parkfinderforispark.ui

import com.mehmetalivargun.parkfinderforispark.data.remote.IsparkApi
import com.mehmetalivargun.parkfinderforispark.data.remote.ParkDetail
import com.mehmetalivargun.parkfinderforispark.data.remote.Parks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject

class MainRepository @Inject constructor(private var api:IsparkApi) {

    suspend fun fetchParks(): Flow<Any> =flow {
        emit(ParkListResult.Loading)
        val response= try {

            api.getAllParks()
        }catch (ex:Exception){
            null
        }

        when(response?.code()){
            null->emit(ParkListResult.Failure)
            200-> {
                val parks=response.body()!!
                emit(ParkListResult.Success(parks))
            }
            401-> emit(ParkListResult.UnexpectedError)
        }
    }

    suspend fun fetchPark(id:Int): Flow<Any> =flow {
        emit(ParkResult.Loading)
        val response= try {

            api.getParkDetail(id)
        }catch (ex:Exception){
            null
        }

        when(response?.code()){
            null->emit(ParkResult.Failure)
            200-> {
                val parks=response.body()!!
                emit(ParkResult.Success(parks))
            }
            401-> emit(ParkResult.UnexpectedError)
        }
    }


    sealed class ParkResult {
        class Success(val parks: ParkDetail) : ParkResult()
        object Failure : ParkResult()
        object UnexpectedError : ParkResult()
        object Loading : ParkResult()
    }
    sealed class ParkListResult {
        class Success(val parks: Parks) : ParkListResult()
        object Failure : ParkListResult()
        object UnexpectedError : ParkListResult()
        object Loading : ParkListResult()
    }
}